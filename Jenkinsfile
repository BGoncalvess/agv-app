@Library('agv-shared-libraries') _

pipeline {
    dependencyCheck()
    parameters{
        booleanParam(name: 'TOGGLE', defaultValue: true, description: 'Run Agv')
        choice(name: 'CHOISE', choices: ['agv-commons','agv-inf','agv-alr','agv-vms','agv-env','agv-ctl','agv-map','agv-pln','agv-usr','agv-veh','agv-fe', 'agv-kpi'], description: 'Run Individualy')
    }
    
    agent any
    
    def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
    def choiseRunAll = sh(script: '${params.TOGGLE}')
    def choiseIndividualy = sh(script: '${params.CHOICE}')

    stages {
        stage('Fetch Latest Tag') {
            when {  
                expression { readYaml(file: 'config.yaml').stages['fetch_latest_tag'] == true } 
            }
            steps {
                script {
                    if (params.RunAgv) {
                        stageFetchLatestTag(choiseRunAll)
                    } else {
                        stageFetchLatestTag(choiseIndividualy)
                    }
                }
            }
        }
        stage('Build Image') { 
            when { 
                expression { readYaml(file: 'config.yaml').stages['build_image']['enabled'] == true }
            }
            steps {
                script {
                    def language = readYaml(file: 'config.yaml').stages['build_image']['language']
                    switch(language) {
                        case 'java':
                            compileJava(latestTag)
                            break
                        case 'python':
                            compilePython()
                            break
                        case 'nodejs':
                            compileNodeJs()
                            break
                        default:
                            error("Unsupported language: ${language}")
                    }
                }
            }
        }

        stage('Save Image') {
            when { 
                expression { readYaml(file: 'config.yaml').stages['save_image'] == true } 
            }
            steps {
                script {
                    def artifactDir = "artifacts-jenkins/"
                    sh """
                        if [ ! -d \"${artifactDir}\" ]; then
                            mkdir -p ${artifactDir}
                        fi
                    """
                    def timestamp = new Date().format("yyyy-MM-dd-HH-mm-ss", TimeZone.getTimeZone('UTC'))
                    def artifactName = "artifact-${timestamp}.tar"
                    def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
                    sh "minikube image save java:${latestTag} ${artifactDir}${artifactName}"
                    archiveArtifacts artifacts: "${artifactDir}${artifactName}", fingerprint: true

                    def maxArtifacts =   5
                    def storedArtifacts = findFiles(glob: "${artifactDir}/*.tar")

                    if (storedArtifacts.size() > maxArtifacts) {
                        storedArtifacts.sort { -it.lastModified() }
                        while (storedArtifacts.size() > maxArtifacts) {
                            def fileToDelete = storedArtifacts.removeLast()
                            sh "rm ${fileToDelete}"
                        }
                    }
                }
            }
        }

        stage('Deploy Image'){
            when { 
                expression { readYaml(file: 'config.yaml').stages['deploy_image'] == true} 
            }
            steps {
                script {
                    sh 'minikube kubectl -- apply -f deployment.yaml'
                    sh 'minikube kubectl -- get namespaces'
                    sh 'minikube kubectl -- get deployments --namespace agv-do-bruno'
                    sh 'minikube kubectl -- get pods --namespace agv-do-bruno'
                }
            }
        }
    }
}

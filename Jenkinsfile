@Library('agv-shared-libraries') _

pipeline {
    parameters{
        booleanParam(name: 'Run All Projects', defaultValue: true, description: 'AGV')
        // choice(name: 'Run Projects Individualy', choices: ['agv-commons','agv-inf','agv-alr','agv-vms','agv-env','agv-ctl','agv-map','agv-pln','agv-usr','agv-veh','agv-fe', 'agv-kpi'], description: 'AGV')
        choice(name: 'Run Projects Individualy', choices: ['agv-app', 'agv-infra'], description: 'AGV')
    }
    
    agent any
    

    stages {
        
        stage('Fetch Latest Tag') {
            when {  
                expression { readYaml(file: 'config.yaml').stages['fetch_latest_tag'] == true } 
            }
            steps {
                script {
                    def choiseRunAll = params.TOGGLE
                    def choiseIndividualy = params.CHOICE

                    if (params.TOGGLE) {
                        stageFetchLatestTag(choiceRunAll)
                    } else {
                        stageFetchLatestTag(choiceIndividualy)
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

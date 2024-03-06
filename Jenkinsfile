@Library('agv-shared-libraries') _

pipeline {
    agent any
    stages {
        stage('Fetch Latest Tag') {
            when {  
                expression { readYaml(file: 'config.yaml').stages['fetch_latest_tag'] == true } 
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'e1b078a5-f9cc-4e1f-9465-c9f042e52e2d', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
                    sh '''
                        git config --global http.https://github.com/BGoncalvess/agv-do-bruno.git "AUTHORIZATION: Basic $(echo -n $GIT_USERNAME:$GIT_TOKEN | base64)"
                        git fetch --tags
                    '''
                    script {
                        def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
                        prepareCodeTest(latestTag)
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
                            def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
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
                    // Check if the directory exists, if not, create it
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
                        // Sort files by last modified time in descending order
                        storedArtifacts.sort { -it.lastModified() }
                        // Delete oldest files until only maxArtifacts remain
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

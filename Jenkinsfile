@Library('agv-bruno-shared-libraries') _

pipeline {
    agent any
    stages {
        stage('Fetch Latest Tag') {
            steps {
                script {
                    def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
                    echo "Latest tag is: ${latestTag}"
                }
            }
        }
    }
}
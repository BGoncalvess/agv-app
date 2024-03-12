def call (Bolean choice){
    if (choise == True){
        withCredentials([usernamePassword(credentialsId: '884e60f4-2593-46b7-8fc0-b8745791ce4a', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
        choise.length() 
        sh '''
            git config --global http.https://github.com/BGoncalvess/agv-commons.git "AUTHORIZATION: Basic $(echo -n $GIT_USERNAME:$GIT_TOKEN | base64)"
            git fetch --tags
        '''
            script {
                def latestTag = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
                prepareCodeTest(latestTag)
            }
        }
    }
}
def call(String tags) {
    echo "Latest Git tag: ${tags}"

    def tagParts = tags.tokenize('.')
    def branchName = env.BRANCH_NAME

    if (branchName.startsWith('feature')) {
        tagParts[1] = tagParts[1].toInteger() + 1
        tagParts[2] = 0
    } else if (branchName.startsWith('hotfix')) {
        tagParts[2] = tagParts[2].toInteger() + 1
    } else {
        echo "Tag name not supported for branch: ${branchName}"
    }

    def newTag = "${tagParts[0]}.${tagParts[1]}.${tagParts[2]}"
    echo "New tag: ${newTag}"

    withCredentials([usernamePassword(credentialsId: '884e60f4-2593-46b7-8fc0-b8745791ce4a', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
        def authorName = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
        def authorEmail = sh(script: 'git log -1 --pretty=%ae', returnStdout: true).trim()

        sh """
            git config --local user.name "${authorName}"
            git config --local user.email "${authorEmail}"
            
            git tag -a ${newTag} -m "Updated tag ${newTag}"
            git push https://${GIT_USERNAME}:${GIT_TOKEN}@github.com/BGoncalvess/agv-app.git --tags
        """
    }
}

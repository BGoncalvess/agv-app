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

    // Now, create and push the new tag to GitHub
    withCredentials([usernamePassword(credentialsId: 'f22bc348-cc9c-4455-aab5-e33412be7604', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
        // Get the latest commit's author name and email
        def authorName = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
        def authorEmail = sh(script: 'git log -1 --pretty=%ae', returnStdout: true).trim()

        sh """
            git config --local user.name "${authorName}"
            git config --local user.email "${authorEmail}"
            
            git tag -a ${newTag} -m "Updated tag ${newTag}"
            git push https://${GIT_USERNAME}:${GIT_TOKEN}@github.com/BGoncalvess/agv-do-bruno.git --tags
        """
    }
}

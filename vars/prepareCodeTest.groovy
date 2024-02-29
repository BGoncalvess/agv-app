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

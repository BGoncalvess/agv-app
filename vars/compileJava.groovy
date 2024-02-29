def call(String latestTag){
    sh "minikube image build -t java:${latestTag} -f ./Dockerfile ."
    sh "sed -i 's/version_tag_placeholder/${latestTag}/g' deployment.yaml"
}

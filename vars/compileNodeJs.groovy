def call(){
    sh 'kubectl create namespace nodejs'
    sh 'minikube image build -t nodejs:latest -f ./Dockerfile .'

}
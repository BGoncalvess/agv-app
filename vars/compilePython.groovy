def call(){
    sh 'kubectl create namespace python'
    sh 'minikube image build -t python:latest -f ./Dockerfile .'
    sh '''
        python3.11 -m venv venv
        source venv/bin/activate
    '''
     sh 'python -m py_compile Teste.py'
}
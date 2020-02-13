properties([[$class: 'JiraProjectProperty'], parameters([string(defaultValue: 'latest', description: 'Please provide a version number', name: 'APP_VERSION', trim: false)])])
node {
    stage("pull repo"){
        git 'https://github.com/farrukh90/nodejs_app.git'
    }
    stage("Build Image"){
        sh "docker build -t app1:${APP_VERSION}  ."
    }
    stage("Image Tag"){
        sh '''docker tag app1:${APP_VERSION} 713287746880.dkr.ecr.us-east-1.amazonaws.com/app1:${APP_VERSION}'''

    }
    stage("Login to ECR"){
        sh '''$(aws ecr get-login --no-include-email --region us-east-1)'''
        
    }
    stage("Push Image"){
        sh "docker images"
        sh "docker push 713287746880.dkr.ecr.us-east-1.amazonaws.com/app1:${APP_VERSION}"
        
    }
    stage("Notify"){
        sh "echo Hello"
    }
}

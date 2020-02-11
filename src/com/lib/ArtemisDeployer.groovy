def deployer(){
  node {
	properties(
        [parameters(
            [choice(choices: 
            [
            'version/0.1', 
            'version/0.2', 
            'version/0.3', 
            'version/0.4', 
            'version/0.5', 
            'version/0.6', 
            'version/0.7', 
            'version/0.8', 
            'version/0.9', 
            'version/0.10'], 
        description: 'Which version of the app should I deploy?', 
        name: 'VERSION'), 
            choice(choices: 
            [
            'dev1.burakkarakas.net', 
            'qa1.burakkarakas.net', 
            'stage1.burakkarakas.net', 
            'prod1.burakkarakas.net'], 
        description: 'Please provide the environment to build the application!', 
        name: 'ENVIR'), 
            choice(choices: 
            [
            'burakdevops@gmail.com ', 
            'burakdevops1@gmail.com', 
            'burakdevops2@gmail.com', 
            'burakdevops3@gmail.com'], 
        description: 'Please choose the email to send notifications!', 
        name: 'EMAIL')])])

	stage("Stage1"){
		timestamps {
			ws {
				checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/fuchicorp/artemis.git']]])
		}
	}
}

	stage("Install Prerequisites"){
		timestamps {
			ws{
				sh '''
					ssh centos@${ENVIR} sudo yum install epel-release -y
					ssh centos@${ENVIR} sudo yum install python-pip -y 
					ssh centos@${ENVIR} sudo pip install Flask
					'''
		}
	}
}

	stage("Copy Artemis"){
		timestamps {
			ws {
				sh '''
					scp -r * centos@${ENVIR}:/tmp
					'''
		}
	}
}

	stage("Run Artemis"){
		timestamps {
			ws {
				sh '''
					ssh centos@${ENVIR} nohup python /tmp/artemis.py  &
					'''
		}
	}
}

	stage("Send slack notifications"){
		timestamps {
			ws {
				echo "Slack"
				//slackSend color: '#BADA55', message: 'Hello, World!'
			}
		}
	}

    stage("Send Email Notification"){
        mail bcc: '', 
        body: '''Hello,
        Artemis application ${Version} has been deployed to $(ENVIR).
        Thanks.''', 
        cc: '', 
        from: '', 
        replyTo: '', 
        subject: 'Artemis application', 
        to: '${EMAIL}'
    }
}
}



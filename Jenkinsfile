pipeline {
    agent any
    environment {
         PROJECT_ID = 'crypto-shore-338809'
        CLUSTER_NAME = 'jenkins-cd'
        LOCATION = 'asia-south1-a'
        CREDENTIALS_ID = 'jenkinsGKE'
        pom = readMavenPom file: 'pom.xml'
        version =pom.version.replaceAll('-SNAPSHOT','')
        registry = "thomrohit/restcrudapi:${version}"
        dockerImage = ''
    }
   triggers {
		pollSCM 'H/10 * * * *'
	}

	options {
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '14'))
	}
    stages {
        stage("Checkout code") {
            steps {
               checkout scm
            }
        }
        stage ("Build"){
			steps {
			bat"mvn clean install"
    		 echo "${registry}"
			}
		}
		stage('Docker Build & Publish') {
              steps{
                  /*  //Facing an issue Scripts not permitted to use unless authorized hence committed out and using a direct docker in local machhine
                  script {
                dockerImage =docker.build registry
                 docker.withRegistry(credentialsId: 'docker_creds', url: '')  {
                  dockerImage.push()
                  }
                } */
                  
                  withCredentials([usernamePassword(credentialsId: 'docker_creds', passwordVariable: 'pass', usernameVariable: 'user')]) {
                    bat """
                    docker login -u $user -p $pass
                    docker build -t $registry .
                    docker push $registry
                                  """
                }
                 
               
              }
            }
         stage('Deploy to GKE') {
            steps{
               sh """
               cd .k8s
               sed -i 's|thomrohit/restcrudapi:latest|$registry|g' curd.api.deployment.yml"""
               step([$class: 'KubernetesEngineBuilder', projectId: env.PROJECT_ID, clusterName: env.CLUSTER_NAME, location: env.LOCATION, manifestPattern: '.k8s', credentialsId: env.CREDENTIALS_ID, verifyDeployments: true])
            }
        }
        
    }
	post {
	    always{
	emailext attachLog: true, body: 'Login to view the entire build: ${BUILD_URL}', mimeType: 'text/html', subject: '$DEFAULT_SUBJECT', to: 'thomrohit@gmail.com'
	}
	}
}

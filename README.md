# Implement a simple key-value HTTP CRUD API service. 

A Simple SpringBoot Maven project that performs the basic CRUD operations such as GET,POST and DELETE, the data is stored/persisted in a objects.json file which is hosted in a volume.

## DevOps Steps 
All scripts to run Kuberentes files are located under **.k8s**. <br/>

Steps to execute in local:
Ensure you have Java, git, maven, docker, kuberenetes installed

1. Checkout code
2. mvn clean install
3. docker build -t username/repo:version .
4. docker push username/repo:version
5. Update **curd.api.deployment.yml** file with image name used from previous
6. Replace pvc.yml with pvc.local.yml located under .k8s/local
7. kubectl apply -f .k8s
8. Curl to test using localhost
   
- curl -X GET http://localhost:8181/
- curl -X POST http://localhost:8181/foo/bar
- curl -X DELETE http://localhost:8181/foo

On GCP the service creates an external IP which can be replaced with localhost to test on cloud 

On GCP the service creates an external IP which can be replaced with localhost to test on cloud 
## DevOps requirements

-	Provide scripts that create initial deploy environment.
    *	`Kubectl apply –f .k8s` (equivalent command in CI/CD pipeline to GKE Cloud) 
-	Provide scripts that creates a new application version.
    *	`mvn release:clean release:prepare`
        -	Updates the version number and creates a tag branch in GitHub
    *	Dockerfile
         - `Docker build –t  username/repo:$version .`
         - `Docker push username/repo:$version` 
-	Provide scripts that can update the deployed application to a new version.
    *	CI/CD pipeline will update the deployment.yaml file with the latest version
         -	`sed -i 's|thomrohit/restcrudapi:latest|$registry:$version|g' curd.api.deployment.yml`
    *	`Kubectl apply –f .k8s` (equivalent command in CI/CD pipeline to GKE Cloud)  
-	Provide scripts that tear down the environment
    *	To delete the entire project in GKE
        - `gcloud projects delete PROJECT_ID`
    *	To delete the cluster
        - `gcloud container clusters delete jenkins-cd --zone= asia-south1-a`

## System Overview
![image](https://user-images.githubusercontent.com/37391853/150505929-88d01921-360b-4dbe-aa33-1943e4f8911f.png)


When the developer makes changes to the code and pushes it into GitHub, a Jenkins pipeline job is triggered following Code checkout, Maven Build, Docker Build and Push into Docker Hub and finally deploy in GCP using GKE(Google Kuberenetes Engine).

## Integrating Jenkins with GKE

Ensure that you have the  following plugins Google Kubernetes Engine Plugin, pipeline-utility-steps, Docker, Kuberenetes in Jenkins

The following steps have to followed  for integration<br/>

Create a project in GKE<br/>
  `gcloud projects create gke-rest-proj --name="Rest Curd Apis"`

Point to the project <br/>
  `gcloud config set project PROJECT_ID`

Create a cluster in GKE<br/>
  `gcloud container clusters create jenkins-cd --zone ZONE `

Enable the following APIs<br/>
 ` gcloud services enable compute.googleapis.com container.googleapis.com cloudbuild.googleapis.com servicemanagement.googleapis.com cloudresourcemanager.googleapis.com --project PROJECT_ID`
  
Create a service account in GKE<br/>

  `gcloud iam service-accounts create jenkins --description="jenkins service account" --display-name="jenkins"`

To grant your service account an IAM role on your project<br/>

  `gcloud projects add-iam-policy-binding PROJECT_ID --member="serviceAccount:jenkins@PROJECT_ID.iam.gserviceaccount.com" --role="roles/container.admin"`

To create the json secret file to connect to GKE from Jenkins<br/>

  `gcloud iam service-accounts keys create secert.json --iam-account=jenkins@PROJECT_ID.iam.gserviceaccount.com`<br/>

copy the key  and add it to **Google Service Account from private key credential** in Jenkins<br/>

For more information/references in detail:
- https://cloud.google.com/architecture/jenkins-on-kubernetes-engine-tutorial
- https://cloud.google.com/iam/docs/creating-managing-service-accounts
- https://cloud.google.com/iam/docs/creating-managing-service-account-keys



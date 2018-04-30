#!/usr/bin/env groovy

pipeline {

  agent any

  //Gitlab plugin configuration to update Gitlab from Jenkins after job completion
  post {
        failure {
          updateGitlabCommitStatus name: 'build', state: 'failed'
        }
        success {
          updateGitlabCommitStatus name: 'build', state: 'success'
        }
  }

  //Gitlab plugin configuration
  options {
    gitLabConnection('Gitlab jenkins connection')
  }

  //Gitlab plugin configuration
  triggers {
    gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: 'All')
  }

  environment {
     jarName = sh(
      script: './gradlew getJarName -q',
      returnStdout: true
     ).trim()
     version = sh(
      script: './gradlew getVersion -q',
      returnStdout: true
     ).trim()
   }

  stages{
    stage('Clean') {
      steps{
        sh './gradlew clean'
      }
    }
    stage('Compile') {
      steps {
        sh './gradlew compileJava'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew test'
      }
    }
    stage('Package') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Release') {
      steps {
       script{
          def splitVersion = version.split('\\.')
          env.majorVersion = splitVersion[0]
          env.minorVersion = (splitVersion.length>1) ? splitVersion[1] : '0'
          env.patchVersion = (splitVersion.length>2) ? splitVersion[2].split("-")[0] : '0'
          if(env.BRANCH_NAME == 'master') { //release and publish in nexus
            sh './gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=0.0.1 -Prelease.newVersion=0.0.1-SNAPSHOT'
          } else { //publish in nexus only
            sh './gradlew publish'
          }
       }
      }
    }
    stage('Deploy') {
      steps {
          script {
            if(env.BRANCH_NAME == 'master') {
              sh 'ssh blogggr@blogggr.com "sudo systemctl stop blogggr"'
              sh 'scp "./back end/build/libs/$jarName" blogggr@blogggr.com:/var/www/blogggr/blogggr.jar'
              sh 'ssh blogggr@blogggr.com "sudo systemctl start blogggr"'
            } else {
              echo 'Deployment skipped!'
            }
          }
      }
    }
  }
}
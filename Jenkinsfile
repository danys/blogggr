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

  stages{
    stage('Clean') {
      sh './gradlew clean'
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
    stage('Deploy') {
      steps {
          echo 'Deploying....'
      }
    }
  }
}
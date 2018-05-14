#!/usr/bin/env groovy

def releaseVersion = 'version'

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
     lastCommitMsg = sh(
       script: 'git log -1 --pretty=%B',
       returnStdout: true
     ).trim()
   }

  stages{
    stage('Clean') {
      steps{
        script{
          if (lastCommitMsg.contains("Gradle release plugin")){
            currentBuild.result = 'SUCCESS'
            return
          } else {
            sh './gradlew clean'
          }
        }
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
            def userInput = input(
             id: 'userInput', message: 'Which release version would you like to create?', parameters: [
             choice(name: 'release_version', choices: 'major\nminor\npatch', description: 'What kind of release would you like to create?')
            ])
            int majorVersion = env.majorVersion as Integer
            int minorVersion = env.minorVersion as Integer
            int patchVersion = env.patchVersion as Integer
            int newMajorVersion = env.majorVersion as Integer
            int newMinorVersion = env.minorVersion as Integer
            int newPatchVersion = env.patchVersion as Integer
            if (userInput == 'major'){
              majorVersion++
              minorVersion=0
              patchVersion=0
              newMajorVersion++
              newMinorVersion=0
              newPatchVersion=1
            } else if (userInput == 'minor'){
              minorVersion++
              patchVersion=0
              newMinorVersion=0
              newPatchVersion=1
            } else if (userInput == 'patch'){
              newPatchVersion++
            }
            releaseVersion = String.valueOf(majorVersion) + "." + String.valueOf(minorVersion) + "." + String.valueOf(patchVersion)
            def newVersion = String.valueOf(newMajorVersion) + "." + String.valueOf(newMinorVersion) + "." + String.valueOf(newPatchVersion) + "-SNAPSHOT"
            sh "./gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=${releaseVersion} -Prelease.newVersion=${newVersion}"
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
              build job: 'blogggr-config/' + env.BRANCH_NAME.replace("/", "%2F"), parameters: [[$class: 'StringParameterValue', name: 'version', value: "${releaseVersion}"]], propagate: false'
            } else {
              echo 'Deployment skipped!'
            }
          }
      }
    }
  }
}
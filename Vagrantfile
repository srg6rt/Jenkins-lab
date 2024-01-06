# -*- mode: ruby -*-
# vi: set ft=ruby :

JNKS_STATIC_IP          =   "192.168.1.175"
WNDS_AGNT_STATIC_IP     =   "192.168.1.176"
LNX_AGNT_STATIC_IP      =   "192.168.1.177"


Vagrant.configure("2") do |config|

  # Master server
  config.vm.define "jenkins-master" do |master|
    master.vm.box = "eurolinux-vagrant/centos-stream-9"
 
    master.vm.hostname = "jenkins-master"
    master.vm.network "public_network", :bridge => "eth0", ip: JNKS_STATIC_IP
    master.vm.provider "virtualbox" do |vb|
      vb.memory = 2048
    end

    master.vm.provision "shell", inline: <<-SHELL

      yum update -y

      echo "INSTALL WGET"
      yum install -y wget

      echo "INSTALL Java"
      yum install -y java-11-openjdk-devel
      export JAVA_HOME=$(dirname $(dirname $(readlink $(readlink $(which javac)))))
      export PATH=$PATH:$JAVA_HOME/bin
      export JRE_HOME=/usr/lib/jvm/jre
      export CLASSPATH=.:$JAVA_HOME/jre/lib:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar

      
      echo "Add Jenkins RPM repository"
      wget -O /etc/yum.repos.d/jenkins.repo http://pkg.jenkins-ci.org/redhat/jenkins.repo
      rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

      echo "INSTALL Jenkins"
      yum -y install jenkins

      echo "INSTALL GIT"
      yum install -y git

      #yum install -y docker

      systemctl enable jenkins
      systemctl start jenkins
    SHELL

    master.vm.network "forwarded_port", guest: 8080, host: 8080
  end

  # Windows agent
  config.vm.define "agent-windows" do |agent|
    agent.vm.box = "hashicorp/windows-2022"
    agent.vm.hostname = "jenkins-agent-windows"
    agent.vm.network "public_network", :bridge => "eth0", ip: WNDS_AGNT_STATIC_IP
    agent.vm.provider "virtualbox" do |vb|
      vb.memory = 2048
    end

    agent.vm.provision "shell", inline: <<-SHELL
      Add-WindowsFeature Web-Server
      Install-Module Jenkins
      Jenkins::AgentInstall
    SHELL

    agent.vm.network "forwarded_port", guest: 8080, host: 8080
  end

  # Linux agent
  config.vm.define "agent-linux" do |agent|
    agent.vm.box = "eurolinux-vagrant/centos-stream-9"
 
    agent.vm.hostname = "jenkins-agent-linux"
    agent.vm.network "public_network", :bridge => "eth0", ip: LNX_AGNT_STATIC_IP
    agent.vm.provider "virtualbox" do |vb|
      vb.memory = 2048
    end

    agent.vm.provision "shell", inline: <<-SHELL
      yum update -y
      yum install -y jenkins-agent
      systemctl enable jenkins-agent
      systemctl start jenkins-agent
    SHELL

    agent.vm.network "forwarded_port", guest: 8080, host: 8080
  end
end

import jenkins.model.*
import hudson.security.*
import hudson.util.*;
import jenkins.install.*;


def instance = Jenkins.getInstance()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin")
instance.setSecurityRealm(hudsonRealm)

def strategy = (GlobalMatrixAuthorizationStrategy) instance.getAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "admin")
instance.setAuthorizationStrategy(strategy)

// Try. Remove if working wizard setup disable stil not working
instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)

instance.save()
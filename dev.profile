def username = System.getProperty('user.name')
def userhome = System.getProperty('user.home')
def hostname = java.net.InetAddress.getLocalHost().getHostName()
ext.cedictDataUrl = "file://${userhome}/Downloads/cedict_1_0_ts_utf-8_mdbg.txt.gz"
ext.dictionaryArtifactDeployType = 'rsync'
ext.artifactRepositoryUrlPrefix = "http://${hostname}:8000/"
ext.dictionaryRegistryServiceUrl = "http://${hostname}:8000/"
ext.dictionaryRegistryRemoteHost = 'localhost'
ext.dictionaryRegistryRemotePath = '/usr/share/juzidian-dictionary-repo'
ext.dictionaryRegistryRemoteUser = username
ext.dictionaryArtifactRemoteHost = 'localhost'
ext.dictionaryArtifactRemotePath = '/usr/share/juzidian-dictionary-repo'
ext.dictionaryArtifactRemoteUser = username


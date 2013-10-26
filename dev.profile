/*
 * Juzidian development mode build profile
 */

def userhome = System.getProperty('user.home')
def hostname = java.net.InetAddress.getLocalHost().getHostName()

/* URL for downloading raw CEDict dictionary data */
ext.cedictDataUrl = "file://${userhome}/Downloads/cedict_1_0_ts_utf-8_mdbg.txt.gz"

/* Protocol for publishing dictionary artifacts */
ext.dictionaryArtifactPublishType = 'rsync'

/* URL prefix for downloading published dictionary artifacts */
ext.dictionaryArtifactUrlPrefix = "http://${hostname}:8000/"

/* URL prefix for downloading dictionary registries */
ext.dictionaryRegistryUrlPrefix = "http://${hostname}:8000/"

/* Remote server details for publishing a dictionary registry */
ext.dictionaryRegistryPublishHost = 'localhost'
ext.dictionaryRegistryPublishPath = '/usr/share/juzidian-dictionary-repo'

/* Remote server details for publishing a compressed dictionary artifact */
ext.dictionaryArtifactPublishHost = 'localhost'
ext.dictionaryArtifactPublishPath = '/usr/share/juzidian-dictionary-repo'

/* Remote server details for publishing web site */
ext.webSitePublishHost = 'localhost'
ext.webSitePublishPath = '/srv/http/juzidian.org'


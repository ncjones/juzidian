/*
 * Juzidian default build profile
 */

def sourceforgeWebHost = 'web.sourceforge.net'

/* URL for downloading raw CEDict dictionary data */
ext.cedictDataUrl = 'http://www.mdbg.net/chindict/export/cedict/cedict_1_0_ts_utf-8_mdbg.txt.gz'

/* Protocol for publishing dictionary artifacts */
ext.dictionaryArtifactPublishType = 'amazon'

/* URL prefix for downloading published dictionary artifacts */
ext.dictionaryArtifactUrlPrefix = 'https://s3.amazonaws.com/juzidian-dictionaries/'

/* URL prefix for downloading dictionary registries */
ext.dictionaryRegistryUrlPrefix = 'http://juzidian.org/dictionaries/'

/* Remote server details for publishing a dictionary registry */
ext.dictionaryRegistryPublishHost = sourceforgeWebHost
ext.dictionaryRegistryPublishPath = '/home/project-web/juzidian/htdocs/dictionaries'

/* Remote server details for publishing web site */
ext.webSitePublishHost = sourceforgeWebHost
ext.webSitePublishPath = '/home/project-web/juzidian/htdocs'


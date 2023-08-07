## [1.2.2]
### Changed
- Fix: fix some null pointer bugs

## [1.2.1]
### Changed
 - Compatible with the version of pro, please be sure to upgrade to this version, otherwise there will be problems installing the two versions together
 - 兼容之后的pro的版本,请大家一定要升级到此版本，不然俩个版本一起安装会出现问题
 - Repair: Fix the problem that when the custom database is successfully connected, click save and report an error
 - Optimization: the database has not disappeared after switching the oracle
 - Optimization: Custom database configuration can now be activated or deactivated with the selection of different types,
  eg: select table, will activate the production action, otherwise it will be disabled
 - Fix: fix some null pointer bugs
## [1.2.0]
### Changed
- 有更多需求的朋友，可以关注接下来发布的pro 版本的插件功能
- 发现此版本关注的人数远远低于预期,再观察一段时间，如果没啥流量,就专心开发pro 版本
- Add advanced function activation (pro version)
- Add mybatis log monitoring and printing, currently does not support jpa logs (pro version)
- Fix the problem that multiple conditions are the same when generating conditional statements
- Fix the problem that the prefix cannot be automatically associated when the mapper method is intelligently prompted
- Fix the problem of generating an error when the mybatis toole database table does not have a primary key
- Fix the problem of incorrect table type conversion in Mybatis tools
- Fix the problem of if test interface still occurs if there is no parameter when generating sql based on method name.
- Fix the problem that the method of mybatis collection select tag is not reported in this xml
- Improve the prompts of xml sql parameters, #{} prompts are more intelligent
## [1.1.9]
### Changed
- Support 2021.2.1
- Support generating if test sql statement
- Fix smart prompt problem


## [1.1.8]
### Changed
-  Support 2021.2
-  Support Mybatis xml collect attribute
-  Change the plug-in HD icon
-  Fix some bugs

## [1.1.7]

### Changed

- Fix: Mybatis Xml does not set the ResultMap generation method to report an error
- Fix: generate the configuration interface, select a module can not load the package path problem under this module
- Improvement: Now you can manually set whether to refer to the Mapper method in other addresses or directly jump to the
  Xml file, which is enabled by default,Setting-->Tool-->Mybatis Smart Plugin for configuration
- Trailer: After the 211.7442.40 version of idea, the api has changed quite a lot, and the minimum supported version of
  the next version will continue to improve

## [1.1.6]

### Changed

- Fix: Add database configuration without downloading the driver, click the connection directly or the application
  reports an error
- new: Added global configuration, which can be configured in Settings-Tools-Mybatis Smart Plugin, and more global
  configuration items will be added in the future
- new: Multi-table generation tool, now supports deleting a table
- new: Start automatic detection of plug-ins that may cause incompatibility, and provide a one-click disable function
- Improvement: when mybatis generator is marked as not generating comments, xml will be generated by appending, and now
  it is improved to overwrite to avoid generating xml errors
- new: support version 2021.1.3
- new: Support language switching, Setting-->Tool-->Mybatis Smart Plugin for configuration
- new: Support for dynamic startup of built-in database configuration, the default IC version is enabled by default, IU
  version is disabled by default, Setting-->Tool-->Mybatis Smart Plugin is configured
- Trailer: After the 211.7442.40 version of idea, the api has changed quite a lot, and the minimum supported version of
  the next version will continue to improve

## [1.1.5]

### Changed

- Fix: When selecting the table to generate code, do not change any fields and report an error
- Fix: Fix some bugs
- New: new: support multi-table generation
- Trailer: After the 211.7442.40 version of idea, the api has changed quite a lot, and the minimum supported version of
  the next version will continue to improve

## [1.1.4]

### Changed

- Community Edition adds code generation through self-developed database management
- Optimization: Optimized to add download progress prompt when adding database download driver
- New: the database now supports oracle
- New: Added support for tk Mapper
- new: support the service mapper method to jump directly to xml


# Nifi custom processor for transform list document to one example
Library name: nifi-compose.transformer-nar

  Пример создания собственного процессора для использования в Apache NiFi.
Процессор берет из исходящей очереди outputDocumentQueue список документов в формате XML и собирает один документ в формате JSON.
Полученный документ выкладывается в очередь outputDocumentListQueue.

Ссылки на использованную документацию:

https://community.cloudera.com/t5/Community-Articles/Building-a-Custom-Processor-Using-IntelliJ/ta-p/244343

## Example
nifi-compose.transformer-nar-1.12.1.nar

## Build
mvn clean install

Ensure NameNode and DataNode are running:
```
jps
```

```
NameNode
DataNode
```

If they are missing, restart Hadoop:

```bash
hdfs --daemon start namenode
hdfs --daemon start datanode
```

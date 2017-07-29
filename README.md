[![Build Status](https://travis-ci.org/ashwanthkumar/manthan-product-affinity.svg?branch=master)](https://travis-ci.org/ashwanthkumar/manthan-product-affinity)
# manthan product affinity

Repository contains the code that was build as part of the  manthan hackathon.

First level of design document for the solution can be found online [here](https://docs.google.com/document/d/139vsPB_nDf2F7adNe6KPLnmXszBsqmEIydIXwflxZL4/edit?usp=sharing).

## Build
Build a fat jar using 
```
mvn clean package
```

You can find the uber jar in `target/` of your repository.

## Usage
Convert the txt file into JSON lines file (for the report)
```
java -Xms4G -Xmx4G  -cp manthan-1.0.0-SNAPSHOT.jar in.ashwanthkumar.manthan.DatasetConverter affinity_1.5M.txt  aggregate_1.5M.jsonl
```

Convert the txt file into JSON lines file (for the graph)
```
java -Xms4G -Xmx4G  -cp manthan-1.0.0-SNAPSHOT.jar in.ashwanthkumar.manthan.DatasetConverterForGraph aggregate_1.5M.jsonl  graph_1.5M.jsonl
```

Run the service pointing to these files
```
java -Xms4G -Xmx4G -cp manthan-1.0.0-SNAPSHOT.jar in.ashwanthkumar.manthan.service.Service server
```

You can then access the report via [`http://localhost:8080/report/view`](http://localhost:8080/report/view).

## License
https://www.apache.org/licenses/LICENSE-2.0

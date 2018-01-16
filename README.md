## Cache Multi Store Includes:

* **MultiKVStore.proto:** Protobuf model of CacheMultiStore
___
*  **MultiKVStore.pb.go:** Protobuf generated GoLang API
*  **MultiKVStore.java:** Protobuf generated Java API 
___
*  **MultiKVStoreMaker.java:** Java test program to make and serialize a simple CacheMultiStore object
*  **CacheMultiStoreBuf.go:** Go test program to deserialize a CacheMultiStore object
*  **MultiKVStoreCompare.java:** Compares the output files of MultiKVStoreMaker and CacheMultiStoreBuf
___
* **Test.bat:** Batch file to compile and run the compare test
___
### MultiKVStore.proto:
There are 4 top-level messages:
* **CacheMultiStoreBuf** contains a CacheKVStore, the current working version ID, the last *CommitID* , and a map of its substores
	* *CommitID* contains the last commit version and its merkle tree node hash 
*  **CacheKVStore** contains a map of strings to CValues and its parent KVStore  
*  **CValue** contains its value, a deleted boolean, and a dirty boolean
*  **KVStore** is a simple database that contains a repeated array of *models*
	* *Model* contains a byte arrays representing a key and value

To use MultiKVStore.proto, navigate the project directory and type into command prompt:
* **GoLang:** "protoc -I=.\src --go_out=.\src\io\cybermiles\cosmos\sdk\store\multiKVStoreBuilder .\src\multiKVStore.proto"
* **Java:**  "protoc -I=.\src --java_out=.\src .\src\multiKVStore.proto"
___
### MultiKVStoreMaker.java:
This creates a simple CacheMultiStore object using the protobuf generated API stored in MultiKVStoreBuilder/MultiKVStore.java to test data integrity after serializing and deserializing under the following constraints:
* There will be anywhere between 1 and 1000 Substores
* Working ID is randomly set between 1 and 200
* Current Version is one less than the Working ID
* The Commit Hash is the Working ID converted to a Byte[ ]
* Each CacheKVStore will contain a random number of cache entries (between 1 and 10) with random data
* Each KVStore will contain between 1 and 10 data entries

After creating the CacheMultiStore object, it is serialized and stored under "MultiKVStoreReciever/multi.my" while the data information is stored under "MultiKVStoreReciever/multi.out"

*Note: to compile, you must compile MultiKVStore.java first and add both Google's Protobuf Jar and the Compiled MultiKVStore path to your ClassPath*
___
### CacheMultiStoreBuf.go:
This deserializes the information passed in from "MultiKVStoreReciever/multi.my"  and stores it in the protobuf generated CacheMultiStoreBuf struct. It then prints out the information stored within the struct to "MultiKVStoreReciever/multi.in"
___
### MultiKVStoreCompare.java:
This compares the outputs in "MultiKVStoreReciever/multi.in" and "MultiKVStoreReciever/multi.out" and prints out any differences in "multi.diff"

If there are any differences to be found, the console will notify you
___
### Test.bat
This calls protobuf to generated Java and Go APIs, compiles and runs MultiKVStoreMaker.java and CacheMultiStoreBuf.go, and compares the files in MultiKVStoreCompare.java 

*Note: Go and JDK should both be added to %PATH% for this to work*
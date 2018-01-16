@echo off
echo.
echo Creating MultiKVStore.pb.go API under "./src/io/cybermiles/cosmos/sdk/store/multiKVStoreBuilder"
protoc -I=.\src --go_out=.\src\io\cybermiles\cosmos\sdk\store\multiKVStoreBuilder .\src\multiKVStore.proto
echo Creating MultiKVStore.java API under "./src/io/cybermiles/cosmos/sdk/store/multiKVStoreBuilder"
protoc -I=.\src --java_out=.\src .\src\multiKVStore.proto
echo.
echo --Running Compare Test--
echo.
echo Compiling MultiKVStore.java
javac -d "bin" -cp ".;D:\willd\Documents\internship\5miles\protobuf-java-3.5.1.jar;" src/io/cybermiles/cosmos/sdk/store/multiKVStoreBuilder/MultiKVStore.java
echo Compiling MultiKVStoreMaker.java
javac -d "src" -cp ".;protobuf-java-3.5.1.jar;bin;" src/io/cybermiles/cosmos/sdk/store/multiKVStoreWrapper/MultiKVStoreMaker.java
echo Running MultiKVStore (Output to "multi.my" and "multi.out")
java -cp ".;protobuf-java-3.5.1.jar;src;" io.cybermiles.cosmos.sdk.store.multiKVStoreWrapper.MultiKVStoreMaker
echo Running CacheMultiStoreBuf.go (Reading from "multi.my" to "multi.in")
go run src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/CacheMultiStoreBuf.go
echo Compiling MultiKVStoreCompare.java
javac -d "src" src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/MultiKVStoreCompare.java
echo Running MultiKVStoreCompare (Output to "multi.dif")
echo.
java -cp ".;src;" io.cybermiles.cosmos.sdk.store.multiKVStoreReciever.MultiKVStoreCompare
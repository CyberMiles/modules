package main

import (
	pb "../multiKVStoreBuilder"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"sort"
	"strconv"

	"github.com/golang/protobuf/proto"
)

func main() {
	in, err := ioutil.ReadFile("multi.my")

	if err != nil {
		log.Fatalln("Error reading file:", err)
	}

	multi := &pb.CacheMultiStoreBuf{}

	marshalErr := proto.Unmarshal(in, multi)

	if marshalErr != nil {
		log.Fatalln("Error reading file:", marshalErr)
	}

	f, err := os.Create("multi.in")

	if err != nil {
		log.Fatalln("Error writing file:", err)
	}

	defer f.Close()

	printMulti(f, multi)
}

func printMulti(f *os.File, multi *pb.CacheMultiStoreBuf) {
	fmt.Fprintln(f, "Working Version:", multi.GetNextVersion())

	printCommitID(f, multi.GetLastCommitID())

	fmt.Fprint(f, "\nCURRENT STORE: \n\n")

	printCacheKVStore(f, multi.GetDb())

	stores := multi.GetSubstores()

	var keys []int
	for k := range stores {
		i, _ := strconv.Atoi(k)
		keys = append(keys, i)
	}

	sort.Ints(keys)

	for _, name := range keys {
		fmt.Fprintf(f, "\nSTORE %d: \n\n", name)
		printCacheKVStore(f, stores[strconv.Itoa(name)])
	}
}

func printCommitID(f *os.File, commit *pb.CacheMultiStoreBuf_CommitID) {
	fmt.Fprintln(f, "Commit Version:", commit.GetVersion())
	fmt.Fprintln(f, "Hash Code:", commit.GetHash())
}

func printCacheKVStore(f *os.File, cache *pb.CacheKVStore) {
	printKVStore(f, cache.GetParent())

	fmt.Fprintln(f, "")

	cvalue := cache.GetCache()

	var keys []int
	for k := range cvalue {
		i, _ := strconv.Atoi(k)
		keys = append(keys, i)
	}

	sort.Ints(keys)

	for _, name := range keys {
		value := cvalue[strconv.Itoa(name)]
		fmt.Fprintf(f, "CValue %d: %v | Deleted: %t | Dirty: %t\n", name, value.GetValue(), value.GetDeleted(), value.GetDirty())
	}
}

func printKVStore(f *os.File, store *pb.KVStore) {
	models := store.GetStore()

	for _, model := range models {
		fmt.Fprintf(f, "{%v , %v}\n", model.GetKey(), model.GetValue())
	}
}

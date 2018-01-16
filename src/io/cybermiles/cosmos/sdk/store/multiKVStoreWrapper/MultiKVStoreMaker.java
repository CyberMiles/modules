package io.cybermiles.cosmos.sdk.store.multiKVStoreWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HandshakeCompletedEvent;

import com.google.protobuf.ByteString;

import io.cybermiles.cosmos.sdk.store.multiKVStoreBuilder.MultiKVStore.CValue;
import io.cybermiles.cosmos.sdk.store.multiKVStoreBuilder.MultiKVStore.CacheKVStore;
import io.cybermiles.cosmos.sdk.store.multiKVStoreBuilder.MultiKVStore.KVStore;
import io.cybermiles.cosmos.sdk.store.multiKVStoreBuilder.MultiKVStore.KVStore.Model;
import io.cybermiles.cosmos.sdk.store.multiKVStoreBuilder.MultiKVStore.CacheMultiStoreBuf;

public class MultiKVStoreMaker {
	private static CacheMultiStoreBuf buildMultiKVStore() {
		Random rand = new Random();
		int randSize = (rand.nextInt(1000/2) + 1) *2;
		
		CacheMultiStoreBuf.Builder multi = CacheMultiStoreBuf.newBuilder();
		
		
		for(int i = 0; i < randSize; i++) {			
			multi.putSubstores(String.valueOf(i), buildCacheKVStore(i));
		}
		
		multi.setDb(buildCacheKVStore(randSize + 1));
		
		long randId = (long) (Math.random() * 200);
		
		multi.setNextVersion(randId);
		
		CacheMultiStoreBuf.CommitID.Builder commitID = CacheMultiStoreBuf.CommitID.newBuilder();
		
		commitID.setVersion(randId-1);
		commitID.setHash(intToBytes((int)randId));
		
		multi.setLastCommitID(commitID.build());
		
		return multi.build();
	}
	
	private static CacheKVStore buildCacheKVStore(int i) {
		Random rand = new Random();
		int randData = (rand.nextInt(10/2) + 1) *2;
		CacheKVStore.Builder cache = CacheKVStore.newBuilder();
		KVStore.Builder store = KVStore.newBuilder();
		
		for(int j = 0; j < randData; j++) {
			Model.Builder model = Model.newBuilder();
			
			model.setKey(intToBytes(j));
			model.setValue(intToBytes(i + j));
			
			store.addStore(model.build());
			
			CValue.Builder cVal = CValue.newBuilder();
			
			cVal.setValue(intToBytes(j));
			
			if((int)(Math.random() * 2) == 1) {
				cVal.setDirty(true);
			}
			else {
				cVal.setDirty(false);
			}
			
			if((int)(Math.random() * 2) == 1) {
				cVal.setDeleted(true);
			}
			else {
				cVal.setDeleted(true);
			}
			
			cache.putCache(String.valueOf(j), cVal.build());
		}
		
		cache.setParent(store.build());
		
		return cache.build();
	}
	
	private static ByteString intToBytes(int convert) {
		return ByteString.copyFrom(ByteBuffer.allocate(4).putInt(convert).array());
	}
	
	private static void printMulti(CacheMultiStoreBuf multi) {
		System.out.println("Working Version: " + multi.getNextVersion());
		printCommitID(multi.getLastCommitID());
		
		System.out.println("\n\nCURRENT STORE: \n");
		printCacheKVStore(multi.getDb());
		
		Map<String, CacheKVStore> stores = multi.getSubstoresMap();
		
		for(String name: stores.keySet()) {
			System.out.println("STORE " + name + ": \n");
			printCacheKVStore(stores.get(name));
		}
	}
	
	private static void printCommitID(CacheMultiStoreBuf.CommitID commit) {
		System.out.println("Commit Version: " + commit.getVersion());
		System.out.print("Hash Code: ");
		printByteString(commit.getHash());
	}
	
	private static void printCacheKVStore(CacheKVStore cache) {
		printKVStore(cache.getParent());
		
		Map<String, CValue> cvalues = cache.getCacheMap();
		
		for(String name : cvalues.keySet()) {
			System.out.print("CValue " + name + ": ");
			printCValue(cvalues.get(name));
		}
		System.out.println("");
	}
	
	private static void printKVStore(KVStore parent) {
		List<KVStore.Model> models = parent.getStoreList();
		
		for(int i = 0; i < models.size(); i++) {
			System.out.print("{");
			printByteString(models.get(i).getKey());
			System.out.print(" , ");
			printByteString(models.get(i).getValue());
			System.out.println("}");
		}
		
		System.out.println("");
	}
	
	private static void printCValue(CValue value) {
		printByteString(value.getValue());
		System.out.println(" | Deleted: " + value.getDeleted() + " | Dirty: " + value.getDirty());
	}
	
	
	private static void printByteString(ByteString byteString) {
		byte[] bytes = byteString.toByteArray();
		
		System.out.print("[");
		for(int i = 0; i < bytes.length; i++) {
			int current = 0;
			
			current |= bytes[i] & 0xFF;
			
			if(i == bytes.length - 1) {
				System.out.print(current);
			}
			else {
				System.out.print(current + " ");
			}
		}
		System.out.print("]");
	}
	
	public static void main(String[] args) throws IOException{
		CacheMultiStoreBuf multi = buildMultiKVStore();
		
		File myFile = new File("./src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/multi.my");
		
		if(!myFile.exists()) {
			myFile.createNewFile();
		}
		
		FileOutputStream output = new FileOutputStream(myFile);
		multi.writeTo(output);

		PrintStream out = new PrintStream(new FileOutputStream("./src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/multi.out"));
		System.setOut(out);	
		printMulti(multi);
	}
}

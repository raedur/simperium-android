package com.simperium.storage;

import com.simperium.StorageProvider;
import com.simperium.Bucket;
import com.simperium.Entity;
import java.util.Map;
import java.util.HashMap;

import android.util.Pair;

public class MemoryStore implements StorageProvider {
    private Map<Pair<String,String>, Entity> entities = new HashMap<Pair<String,String>, Entity>();
    private Map<Pair<String,String>, Integer> versions = new HashMap<Pair<String,String>, Integer>();
    private Map<String, String> bucketVersions = new HashMap<String,String>();
    
    // Prepare the datastore to store entites for a bucket
    public void initialize(Bucket bucket){
        
    }
    
    private Pair bucketKey(Bucket bucket, String key){
        return Pair.create(bucket.getName(), key);
    }
    public void addEntity(Bucket bucket, String key, Entity entity){
        Pair bucketKey = bucketKey(bucket, key);
        entities.put(bucketKey, entity);
        versions.put(bucketKey, entity.getVersion());
    }
    public void removeEntity(Bucket bucket, String key){
    }
    public Entity getEntity(Bucket bucket, String key){
        return entities.get(bucketKey(bucket, key));
    }
    public Boolean containsKey(Bucket bucket, String key){
        return entities.containsKey(bucketKey(bucket, key));
    }
    public Boolean hasKeyVersion(Bucket bucket, String key, Integer version){
        Integer localVersion = getKeyVersion(bucket, key);
        return localVersion != null && localVersion >= version;
    }
    public Integer getKeyVersion(Bucket bucket, String key){
        return versions.get(bucketKey(bucket, key));
    }
    public String getChangeVersion(Bucket bucket){
        return bucketVersions.get(bucket.getName());
    }
    public Boolean hasChangeVersion(Bucket bucket){
        return bucketVersions.containsKey(bucket.getName());
    }
    public Boolean hasChangeVersion(Bucket bucket, String version){
        String localVersion = bucketVersions.get(bucket.getName());
        return version == localVersion;
    }
    public void setChangeVersion(Bucket bucket, String string){
        bucketVersions.put(bucket.getName(), string);
    }
    
}
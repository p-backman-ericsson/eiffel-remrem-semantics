
package com.ericsson.eiffel.semantics.events;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EiffelArtifactPublishedEvent
    extends Event
{

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("meta")
    @Expose
    private Meta meta;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("data")
    @Expose
    private ArtifactPublishedData data;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("links")
    @Expose
    private List<Link> links = new ArrayList<Link>();

    /**
     * 
     * (Required)
     * 
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * 
     * (Required)
     * 
     */
    public ArtifactPublishedData getData() {
        return data;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setData(ArtifactPublishedData data) {
        this.data = data;
    }

    /**
     * 
     * (Required)
     * 
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

}

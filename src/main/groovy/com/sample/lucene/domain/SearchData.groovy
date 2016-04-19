package com.sample.lucene.domain

class SearchData {
    String term
    boolean force
    int baseNumber
    
    List results
    int resultsSize
    
    public String getTerm() {
        return term
    }
 
    public void setTerm(String term) {
        this.term = term
    }
    
    public boolean getForce() {
        return force
    }
    
    public void setForce(boolean force) {
        this.force = force
    }
 
    public int getBaseNumber() {
        return baseNumber
    }
 
    public void setBaseNumber(int baseNumber) {
        this.baseNumber = baseNumber
    }
    
    public List getResults() {
        return results ?: []
    }
 
    public void setResults(List results) {
        this.results = results ?: []
    }
    
    public int getResultsSize() {
        return results ? results.size : 0
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Utils.DiffSpelling.Diff;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author C. Levallois
 */
public class TestDiff {

    public static void main(String[] args) {
        
        LinkedList<Diff> diffs = new DiffSpelling().diff_main("allie", "alloe");
        
        Iterator<Diff> diffsIterator = diffs.iterator();
        
        while (diffsIterator.hasNext()){
            
            Diff currDiff = diffsIterator.next();
            System.out.println("currDiff: "+ currDiff.operation.toString() +": "+currDiff.text);
        }
;
            System.out.println("new text: "+new DiffSpelling().diff_text1Custom(diffs));
            System.out.println("new text: "+new DiffSpelling().diff_text2Custom(diffs));
        
    }
}
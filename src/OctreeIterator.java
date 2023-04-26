import java.util.ArrayDeque;
import java.util.Iterator;

// This class collects all the bodies in an Octree in a recursive manner
// On the highest level, it will thus provide an Iterator over a List
// The time complexity of moving through an Octree in such fashion is O(n)
public class OctreeIterator implements Iterator<Body> {

    // Save a list of all Bodies contained in an Octree
    private ArrayDeque<Body> list;

     OctreeIterator(Octree source) {
        list = new ArrayDeque<>();

        switch(source.getFlag()) {
            case NULL:
                break;
     // If the source Octree is a leaf, it contains only 1 Body
            case LEAF:
                list.add(source.getLeafBody());
                break;
     // If the source Octree is a Gate, generate Iterators of its sub-Trees,
     // and add those to the list
            case GATE:
                Iterator<Body> gate;
                for(int i=0; i<source.getGates().length; i++) {
                    gate = source.getGates()[i].iterator();
                    while(gate.hasNext()) {
                        list.addFirst(gate.next());
                    }
                }
                break;
        }
    }

    @Override
    public boolean hasNext() {
        return !list.isEmpty();
    }

    @Override
    public Body next() {
        return list.pollLast();
    }
}

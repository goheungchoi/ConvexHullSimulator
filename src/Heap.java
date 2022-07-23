package Project1;

public class Heap<E extends Comparable<E>>
{
    private java.util.ArrayList<E> list = new java.util.ArrayList<>();
    public Heap()
    {}
    public Heap(E[] objects)
    {
        //Call the add method to create the heap
        for (int i = 0; i < objects.length; i++)
            add(objects[i]);
    }
    //Add element to the heap
    public void add(E obj)
    {
        list.add(obj);
        int currentIndex = list.size() - 1;
        while (currentIndex > 0)
        {
            int parentIndex = (currentIndex - 1) / 2;
            // Swap if the current obj is greater than its parent
            if (list.get(currentIndex).compareTo(list.get(parentIndex)) > 0)
            {
                E temp = list.get(currentIndex);
                list.set(currentIndex, list.get(parentIndex));
                list.set(parentIndex, temp);
            }
            else
                break; //tree is a heap
            currentIndex = parentIndex;
        }
    }
    //Remove the root from the heap
    public E remove()
    {
        if (list.size() == 0)
            return null;
        E removedObj = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.remove(list.size() - 1);
        
        int currentIndex = 0;
        while(currentIndex < list.size())
        {
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;
            //Find the max of these two children
            if (leftChildIndex >= list.size())
                break;
            int maxIndex = leftChildIndex;
            if (rightChildIndex < list.size())
            {
                if (list.get(maxIndex).compareTo(list.get(rightChildIndex)) < 0)
                    maxIndex = rightChildIndex;
            }
            //Swap if current node is less than the maximum
            if (list.get(currentIndex).compareTo(list.get(maxIndex)) < 0)
            {
                E temp = list.get(maxIndex);
                list.set(maxIndex, list.get(currentIndex));
                list.set(currentIndex, temp);
                currentIndex = maxIndex;
            }
            else
                break;  //tree is a heap
        }
        return removedObj;
    }
    public int getSize()
    {
        return list.size();
    }
}

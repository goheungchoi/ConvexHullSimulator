package Project1;

import ChansAlgorithm.*;

/**
 *
 * @author Goheung Choi
 * @param <E>
 */
//Encapsulats the stack ADT and provides operations for msnipulating stacks
public class Stack<E>
{
    private java.util.ArrayList<E> list = new java.util.ArrayList<>();

    public int getSize() 
    {
        return list.size();
    }

    public E peek()
    {
        return list.get(getSize() - 1);
    }

    public E peek_first()
    {
        return list.get(getSize() - 1);
    }
    
    public E peek_second()
    {
        return list.get(getSize() - 2);
    }
        
    public E peek_from_back(int num)
    {
        return list.get(getSize() - num);
    }
    
    public void push(E o) 
    {
        list.add(o);
    }

    public E pop() 
    {
        E o = list.get(getSize() - 1);
        list.remove(getSize() - 1);
        return o;
    }
    
    public void clear()
    {
        list.clear();
    }

    public boolean isEmpty() 
    {
        return list.isEmpty();
    }

    public java.util.ArrayList<E> toArrayList()
    {
        return list;
    }
    @Override
    public String toString() 
    {
        return "stack: " + list.toString();
    }
}
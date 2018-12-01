package amata1219.hogochi.byebye;

import java.util.ArrayList;
import java.util.List;

public class ImageMap {

	private boolean[] args;
	//5, 6, 14, 15 <-> 1, 2, 3, 4

	public ImageMap(boolean[] args){
		this.args = args;
	}

	public void update(boolean[] args){
		this.args = args;
	}

	public List<Integer> getSlotNumbers(){
		List<Integer> list = new ArrayList<>();

		if(is(0))
			list.add(5);

		if(is(1))
			list.add(6);

		if(is(2))
			list.add(14);

		if(is(3))
			list.add(15);

		return list;
	}

	public int size(){
		int count = 0;

		for(boolean bool : args)
			count += bool ? 1 : 0;

		return count;
	}

	public boolean is(int n){
		return args[n];
	}

	public boolean isNonSelected(){
		return size() == 1;
	}

	public boolean isDiagonal(){
		if(size() != 2)
			return false;

		return (is(0) && is(3)) || (is(1) && is(2));
	}

	public boolean isL(){
		return size() == 3;
	}

	public boolean isAllSelected(){
		return size() == 4;
	}
}

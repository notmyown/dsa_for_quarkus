package de.nmo.dsa.roller.services;

import java.util.List;

public interface DataService<T> {

    public T get(long id);

    public List<T> all();

    public T create(T t);

    public boolean update(long id, T t);

    public T delete(T t);

}

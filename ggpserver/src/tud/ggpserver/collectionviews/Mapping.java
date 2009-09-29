package tud.ggpserver.collectionviews;

public interface Mapping<T1, T2> {

	T2 map(T1 o);

	T1 reverseMap(T2 o);

}

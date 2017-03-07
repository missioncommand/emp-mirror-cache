package mil.emp3.mirrorcache.event;

public abstract class MirrorCacheEvent<T> {

    static public class Type<T> {
        static private int nextHashCode;
        final private int index;

        public Type() {
            index = ++nextHashCode;
        }
        @Override
        public final int hashCode() {
            return index;
        }
    }

    abstract public void dispatch(T handler);
    abstract public Type<T> getType();
}

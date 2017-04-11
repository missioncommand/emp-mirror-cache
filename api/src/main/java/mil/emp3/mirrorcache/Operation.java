package mil.emp3.mirrorcache;

public interface Operation {

    <T> T as(Class<T> type);
    String name();
}

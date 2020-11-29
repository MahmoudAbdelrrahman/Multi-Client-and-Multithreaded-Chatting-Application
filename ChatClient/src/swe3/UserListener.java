package swe3;

public interface UserListener {
    public void online(String login);
    public void offline(String login);
}

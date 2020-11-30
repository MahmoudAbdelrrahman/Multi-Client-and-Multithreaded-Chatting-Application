package swe3;

public interface UserStatusListener {
    public void online(String login);
    public void offline(String login);
}

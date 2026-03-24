package places.service;

import java.util.List;
import places.model.Friendship;
import places.model.User;

public interface FriendshipManager {
    Friendship sendFriendRequest(String requesterId, String addresseeId);
    List<Friendship> getPendingRequests(String userId);
    Friendship acceptFriendRequest(String friendshipId);
    void declineFriendRequest(String friendshipId);
    List<User> getFriends(String userId);
    List<User> searchUsers(String query);
    void removeFriend(String userId, String friendId);
    List<places.model.FriendRequestDto> getAllFriendRequests(String userId);
    List<String> getSentPendingRequestAddresseeIds(String userId);
}

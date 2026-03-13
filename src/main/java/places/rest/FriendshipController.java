package places.rest;

import static places.constants.Constants.REST_URL;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import places.model.FriendRequest;
import places.model.FriendRequestDto;
import places.model.Friendship;
import places.model.User;
import places.service.FriendshipManager;

@Slf4j
@RestController
@RequestMapping(value = REST_URL)
public class FriendshipController {

    public static final String SEND_FRIEND_REQUEST = "/friendships/request";
    public static final String GET_PENDING_REQUESTS = "/friendships/pending/{userId}";
    public static final String ACCEPT_FRIEND_REQUEST = "/friendships/{friendshipId}/accept";
    public static final String DECLINE_FRIEND_REQUEST = "/friendships/{friendshipId}/decline";
    public static final String GET_FRIENDS = "/friendships/{userId}";
    public static final String REMOVE_FRIEND = "/friendships/remove/{friendId}";
    public static final String GET_ALL_FRIEND_REQUESTS = "/friendships/requests/{userId}";
    public static final String GET_SENT_PENDING_REQUESTS = "/friendships/sent/{userId}";

    @Autowired
    private FriendshipManager friendshipManager;

    @PostMapping(value = SEND_FRIEND_REQUEST)
    public Friendship sendFriendRequest(@RequestBody FriendRequest request) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return friendshipManager.sendFriendRequest(loggedUser.getId(), request.getAddresseeId());
    }

    @GetMapping(value = GET_PENDING_REQUESTS)
    public List<Friendship> getPendingRequests(@PathVariable String userId) {
        return friendshipManager.getPendingRequests(userId);
    }

    @PostMapping(value = ACCEPT_FRIEND_REQUEST)
    public Friendship acceptFriendRequest(@PathVariable String friendshipId) {
        return friendshipManager.acceptFriendRequest(friendshipId);
    }

    @PostMapping(value = DECLINE_FRIEND_REQUEST)
    public void declineFriendRequest(@PathVariable String friendshipId) {
        friendshipManager.declineFriendRequest(friendshipId);
    }

    @GetMapping(value = GET_FRIENDS)
    public List<User> getFriends(@PathVariable String userId) {
        return friendshipManager.getFriends(userId);
    }

    @DeleteMapping(value = REMOVE_FRIEND)
    public void removeFriend(@PathVariable String friendId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        friendshipManager.removeFriend(loggedUser.getId(), friendId);
    }

    @GetMapping(value = GET_ALL_FRIEND_REQUESTS)
    public List<FriendRequestDto> getAllFriendRequests(@PathVariable String userId) {
        return friendshipManager.getAllFriendRequests(userId);
    }

    @GetMapping(value = GET_SENT_PENDING_REQUESTS)
    public List<String> getSentPendingRequests(@PathVariable String userId) {
        return friendshipManager.getSentPendingRequestAddresseeIds(userId);
    }
}

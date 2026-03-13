package places.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import places.model.FriendRequestDto;
import places.model.Friendship;
import places.model.FriendshipStatus;
import places.model.User;
import places.repository.FriendshipRepository;
import places.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FriendshipManagerImpl implements FriendshipManager {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Friendship sendFriendRequest(String requesterId, String addresseeId) {
        boolean alreadyFriends = friendshipRepository
                .findByRequesterIdAndAddresseeId(requesterId, addresseeId)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseGet(() -> friendshipRepository
                        .findByRequesterIdAndAddresseeId(addresseeId, requesterId)
                        .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                        .orElse(false));

        if (alreadyFriends) {
            throw new RuntimeException("Already friends");
        }

        Friendship friendship = Friendship.builder()
                .id(UUID.randomUUID().toString().split("-")[0])
                .requesterId(requesterId)
                .addresseeId(addresseeId)
                .status(FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return friendshipRepository.save(friendship);
    }

    @Override
    public List<Friendship> getPendingRequests(String userId) {
        return friendshipRepository.findByAddresseeIdAndStatus(userId, FriendshipStatus.PENDING);
    }

    @Override
    public Friendship acceptFriendRequest(String friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    @Override
    public void declineFriendRequest(String friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    @Override
    public List<User> getFriends(String userId) {
        List<Friendship> asRequester = friendshipRepository
                .findByRequesterIdAndStatus(userId, FriendshipStatus.ACCEPTED);
        List<Friendship> asAddressee = friendshipRepository
                .findByAddresseeIdAndStatus(userId, FriendshipStatus.ACCEPTED);

        return Stream.concat(asRequester.stream(), asAddressee.stream())
                .map(f -> f.getRequesterId().equals(userId) ? f.getAddresseeId() : f.getRequesterId())
                .map(friendId -> userRepository.findById(friendId).orElse(null))
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchUsers(String query) {
        Query mongoQuery = new Query();
        mongoQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("firstName").regex(query, "i"),
                Criteria.where("lastName").regex(query, "i"),
                Criteria.where("email").regex(query, "i"),
                Criteria.where("username").regex(query, "i"),
                Criteria.where("tag").regex(query, "i")
        ));
        return mongoTemplate.find(mongoQuery, User.class);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        Optional<Friendship> friendship = friendshipRepository
                .findByRequesterIdAndAddresseeId(userId, friendId);
        if (friendship.isEmpty()) {
            friendship = friendshipRepository.findByRequesterIdAndAddresseeId(friendId, userId);
        }
        friendship.ifPresent(friendshipRepository::delete);
    }

    @Override
    public List<String> getSentPendingRequestAddresseeIds(String userId) {
        return friendshipRepository.findByRequesterIdAndStatus(userId, FriendshipStatus.PENDING)
                .stream()
                .map(Friendship::getAddresseeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getAllFriendRequests(String userId) {
        return friendshipRepository.findByAddresseeId(userId).stream()
                .map(f -> {
                    User requester = userRepository.findById(f.getRequesterId()).orElse(null);
                    return FriendRequestDto.builder()
                            .friendshipId(f.getId())
                            .requesterId(f.getRequesterId())
                            .requesterName(requester != null ? requester.getFullName() : "")
                            .requesterTag(requester != null ? requester.getTag() : "")
                            .requesterUsername(requester != null ? requester.getUsername() : "")
                            .requesterProfileImageUrl(requester != null ? requester.getProfileImageUrl() : null)
                            .status(f.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

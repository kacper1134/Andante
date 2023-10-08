package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "User email can't be empty")
    private String email;
    @NotBlank(message = "User name can't be empty")
    private String name;
    @NotBlank(message = "User surname can't be empty")
    private String surname;
    @NotBlank(message = "Username can't be empty")
    private String username;
    private Set<Long> posts;
    private Set<Long> responses;
    private Set<Long> likedPosts;
    private Set<Long> likedResponses;
}

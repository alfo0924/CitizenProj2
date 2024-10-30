package org.example._citizenproj2.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example._citizenproj2.model.Movie;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "電影名稱不能為空")
    @Size(max = 100, message = "電影名稱長度不能超過100")
    private String movieName;

    private String originalName;

    @Size(max = 100, message = "導演名稱長度不能超過100")
    private String director;

    private String cast;

    @NotNull(message = "片長不能為空")
    @Min(value = 1, message = "片長必須大於0")
    private Integer duration;

    private String description;

    @NotNull(message = "上映日期不能為空")
    @Future(message = "上映日期必須是未來的日期")
    private Date releaseDate;

    private Date endDate;

    @Size(max = 10, message = "分級長度不能超過10")
    private String rating;

    private String language;
    private String subtitle;
    private String posterUrl;
    private String trailerUrl;

    @NotNull(message = "類別ID不能為空")
    private Long categoryId;

    private Movie.MovieStatus movieStatus;

    // 驗證方法
    public void validateDates() {
        if (endDate != null && releaseDate != null && endDate.before(releaseDate)) {
            throw new IllegalArgumentException("下檔日期不能早於上映日期");
        }
    }
}
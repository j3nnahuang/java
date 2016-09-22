import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TestParams implements Serializable {
    static final long serialVersionUID = 79469696963812654L;

    Map<String, Double> rasterParams;
    Map<String, Double> routeParams;
    String prefixSearchParam;
    String actualSearchParam;
    Map<String, Object> rasterResult;
    byte[] rasterOutput;
    List<Long> routeResult;
    byte[] routeRaster;
    List<Map<String, Object>> actualSearchResult;
    List<String> autocompleteResults;
}

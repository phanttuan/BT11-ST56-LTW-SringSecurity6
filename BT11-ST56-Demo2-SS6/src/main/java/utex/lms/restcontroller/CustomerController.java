package utex.lms.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utex.lms.entity.UserInfo;
import utex.lms.repository.UserInfoRepository;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @GetMapping("/all")
    public List<UserInfo> getAllCustomers() {
        return userInfoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfo> getCustomerById(@PathVariable("id") Integer id) {
        return userInfoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
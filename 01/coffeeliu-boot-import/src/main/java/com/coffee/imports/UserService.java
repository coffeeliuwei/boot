package com.coffee.imports;


import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import({UserBean.class})
public class UserService {

}

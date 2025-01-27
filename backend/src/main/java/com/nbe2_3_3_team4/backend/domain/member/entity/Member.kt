package com.nbe2_3_3_team4.backend.domain.member.entity

import com.nbe2_3_3_team4.backend.domain.car.entity.Car
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.LoginType
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role
import com.nbe2_3_3_team4.backend.global.BaseTime
import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.persistence.*

@Entity
@Table(name = "members")
class Member() : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id: Long? = null

    @Column(name = "name")
    var name: String? = null

    @Column(name = "email")
    var email: String? = null

    @Column(name = "password")
    var password: String? = null

    @Column(name = "contact")
    var contact: String? = null

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var role: Role? = null

    @Column(name = "social_id")
    var socialId: Long? = null

    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    var loginType: LoginType? = null

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "member_id")
    val cars: MutableList<Car> = ArrayList()

    constructor(name: String, email: String, password :String, contact: String, role: Role,loginType: LoginType) : this() {
        this.name = name
        this.email = email
        this.password = password
        this.contact = contact
        this.role = role
        this.loginType = loginType
    }

    fun updateMember(name: String?, contact: String?) {
        this.name = name
        this.contact = contact
    }

    fun addCar(car: Car): Car {
        cars.add(car)
        return car
    }
    companion object {
        @JvmStatic
		fun to(dto: MemberRequest.Join, passwordEncoder: PasswordEncoder, role: Role): Member {
            return Member(name= dto.name, email = dto.email, password = passwordEncoder.encode(dto.password), contact = dto.contact, role= role, loginType = LoginType.BASIC )
        }
    }
}

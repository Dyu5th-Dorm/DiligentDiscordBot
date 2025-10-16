package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.StudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    final DiscordLinkService dcService;
    final StudentService studentService;
    final LivingRecordService lrService;
    final RoleOperation roleOperation;

    @Value("${regexp.email}")
    String mailSyntax;

    @Value("${regexp.phone_number}")
    String phoneSyntax;

    public AuthErrorType authByPhone(Member member, String studentId, String phoneNumber) {
        if (!phoneNumber.matches(phoneSyntax)) {
            return AuthErrorType.Phone_Syntax;
        }

        return check(member, studentId, phoneNumber);
    }

    public AuthErrorType authMail(Member member, String studentId, String mail) {
        if (!mail.matches(mailSyntax)) {
            return AuthErrorType.Mail_Syntax;
        }

        return this.check(member, studentId, mail);
    }

    AuthErrorType check(Member member, String studentId, String str) {
        if (str.isEmpty() || studentId.isEmpty()) {
            return AuthErrorType.Empty;
        }

        if (dcService.isLinked(member.getId())) {
            return AuthErrorType.Linked;
        }

        if (!studentService.exists(studentId)) {
            return AuthErrorType.Not_Student;
        }

        if (dcService.isLinkByStudentId(studentId)) {
            return AuthErrorType.Linked_By_Other;
        }

        return this.saveInformation(member, studentId, str);
    }

    AuthErrorType saveInformation(Member member, String studentId, String str) {
        Optional<LivingRecord> livingRecordFound = lrService.findByStudentId(studentId);
        if (livingRecordFound.isEmpty()) {
            return AuthErrorType.Not_Current_living;
        }

        String bedId = livingRecordFound.get().getBed().getBedId();

        dcService.link(member.getId(), studentId);

        if (str.matches(mailSyntax)) {
            studentService.saveStudentEmail(studentId, str);
        } else {
            studentService.saveStudentPhoneNumber(studentId, str);
        }

        roleOperation.addRoleToMemberByFloor(member.getGuild(), member, bedId);
        livingRecordFound.map(LivingRecord::getStudent)
                .map(org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student::getCitizenship)
                .ifPresent(citizenship -> roleOperation.addRoleByCitizenship(member.getGuild(), member, citizenship));

        return AuthErrorType.NONE;
    }
}

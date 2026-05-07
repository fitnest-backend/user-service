package az.fitnest.user.controller;

import az.fitnest.user.dto.request.BlockCustomerRequest;
import az.fitnest.user.dto.response.CustomerAccessPolicyResponse;
import az.fitnest.user.dto.response.CustomerStatusResponse;
import az.fitnest.user.model.entity.CustomerStatusHistory;
import az.fitnest.user.service.CustomerAccessPolicyService;
import az.fitnest.user.service.CustomerStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@Tag(name = "Müştəri Məhdudiyyət Kontrolörü", description = "Müştərilərin bloklanması və access policy idarəetməsi")
@SecurityRequirement(name = "bearerAuth")
public class AdminCustomerRestrictionController {

    private final CustomerStatusService customerStatusService;
    private final CustomerAccessPolicyService customerAccessPolicyService;

    @Operation(summary = "Müştərini blokla", description = "Admin tərəfindən müştərini BLOCKED statusuna keçirir")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{customerId}/block")
    public ResponseEntity<CustomerStatusResponse> blockCustomer(
            @Parameter(description = "Müştəri ID-si", example = "1001") @PathVariable Long customerId,
            @Valid @RequestBody BlockCustomerRequest request) {
        // TODO: adminId JWT-dən götürülməlidir
        Long adminId = 1L;
        return ResponseEntity.ok(customerStatusService.blockCustomer(customerId, request, adminId));
    }

    @Operation(summary = "Müştərinin blokunu aç", description = "BLOCKED statusundakı müştərini ACTIVE edir")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{customerId}/unblock")
    public ResponseEntity<Void> unblockCustomer(
            @Parameter(description = "Müştəri ID-si", example = "1001") @PathVariable Long customerId) {
        // TODO: adminId JWT-dən götürülməlidir
        Long adminId = 1L;
        customerStatusService.unblockCustomer(customerId, adminId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Müştəri statusunu gətir", description = "Müştərinin hazırkı statusunu qaytarır")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}/status")
    public ResponseEntity<CustomerStatusResponse> getCustomerStatus(
            @Parameter(description = "Müştəri ID-si", example = "1001") @PathVariable Long customerId) {
        return ResponseEntity.ok(customerStatusService.getCustomerStatus(customerId));
    }

    @Operation(summary = "Müştəri status tarixçəsi", description = "Müştərinin bütün status dəyişikliklərini qaytarır")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}/status/history")
    public ResponseEntity<List<CustomerStatusHistory>> getStatusHistory(
            @Parameter(description = "Müştəri ID-si", example = "1001") @PathVariable Long customerId) {
        return ResponseEntity.ok(customerStatusService.getStatusHistory(customerId));
    }

    @Operation(summary = "Müştəri access policy", description = "Müştərinin hansı əməliyyatları edə biləcəyini qaytarır")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{customerId}/access-policy")
    public ResponseEntity<CustomerAccessPolicyResponse> getAccessPolicy(
            @Parameter(description = "Müştəri ID-si", example = "1001") @PathVariable Long customerId) {
        return ResponseEntity.ok(customerAccessPolicyService.getAccessPolicy(customerId));
    }
}

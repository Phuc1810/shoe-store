/**
 * JavaScript cho quản lý sản phẩm Admin
 */

// Hỗ trợ DELETE method qua Thymeleaf form
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý form có _method = DELETE
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        const methodInput = form.querySelector('input[name="_method"]');
        if (methodInput && methodInput.value === 'DELETE') {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const formAction = this.action;
                const formData = new FormData(this);
                
                // Gửi DELETE request
                fetch(formAction, {
                    method: 'DELETE',
                    headers: {
                        'Accept': 'application/json'
                    }
                }).then(response => {
                    if (response.ok) {
                        // Chuyển hướng về trang danh sách
                        window.location.href = '/admin/products';
                    } else {
                        alert('Có lỗi khi xóa sản phẩm');
                    }
                }).catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi khi xóa sản phẩm');
                });
            });
        }
    });

    // Auto-close alert messages sau 5 giây
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

/**
 * Hàm kiểm tra xem slug đã tồn tại chưa (AJAX validation)
 */
function checkSlugExists(slug, excludeId = null) {
    const params = new URLSearchParams({
        slug: slug
    });
    
    if (excludeId) {
        params.append('excludeId', excludeId);
    }
    
    return fetch('/api/products/check-slug?' + params.toString(), {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(response => response.json())
    .catch(error => {
        console.error('Error checking slug:', error);
        return { exists: false };
    });
}

/**
 * Hàm format tiền tệ (VND)
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

/**
 * Hàm tạo slug từ tên sản phẩm (UTF-8)
 */
function generateSlug(text) {
    if (!text) return '';
    
    // Chuẩn hóa Unicode
    const normalized = text.normalize('NFD')
        .replace(/[\u0300-\u036f]/g, ''); // Xóa dấu
    
    // Chuyển thành slug
    return normalized
        .toLowerCase()
        .trim()
        .replace(/[^a-z0-9\s-]/g, '') // Xóa ký tự đặc biệt
        .replace(/\s+/g, '-') // Thay khoảng trắng bằng dấu -
        .replace(/-+/g, '-') // Xóa dấu - dư thừa
        .replace(/^-|-$/g, ''); // Xóa - ở đầu/cuối
}

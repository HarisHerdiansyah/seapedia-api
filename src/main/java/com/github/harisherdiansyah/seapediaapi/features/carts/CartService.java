package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.exception.UnauthorizedException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductEntity;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductRepository;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreEntity;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreRepository;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public GetCartResponseDTO getAllUserCartItems() {
        UUID userId = SecurityUtil.getCurrentUserId();
        CartEntity cartEntity = cartRepository.findCartEntityByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        StoreEntity storeEntity = cartEntity.getStore();

        UUID cartId = cartEntity.getId();
        List<CartItemQueryResult> queryResult = cartItemRepository.findCartItemsByCartId(cartId);
        List<CartItemsData> cartItemsDataList = queryResult.stream()
                .map(result -> new CartItemsData(
                        result.productId(),
                        result.productName(),
                        result.productImage(),
                        result.quantity(),
                        result.price().multiply(BigDecimal.valueOf(result.quantity()))
                ))
                .toList();

        BigDecimal totalPrice = cartItemsDataList.stream()
                .map(CartItemsData::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new GetCartResponseDTO(storeEntity.getId(), storeEntity.getStoreName(), totalPrice, cartItemsDataList);
    }

    private MutateCartResponseDTO mutateCartInit(UUID userId, MutateCartRequestDTO mutateCartRequestDTO) {
        UserEntity userEntity = userRepository.getReferenceById(userId);
        StoreEntity storeEntity = storeRepository.getReferenceById(mutateCartRequestDTO.getStoreId());
        CartEntity cartEntity = CartEntity.builder().user(userEntity).store(storeEntity).build();
        cartRepository.save(cartEntity);

        ProductEntity productEntity = productRepository.findProductEntityById(mutateCartRequestDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + mutateCartRequestDTO.getProductId()));

        CartItemsEntity cartItemsEntity = CartItemsEntity.builder()
                .cart(cartEntity)
                .product(productEntity)
                .quantity(mutateCartRequestDTO.getQuantity())
                .build();
        cartItemRepository.save(cartItemsEntity);

        return new MutateCartResponseDTO(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice().multiply(BigDecimal.valueOf(mutateCartRequestDTO.getQuantity())),
                mutateCartRequestDTO.getQuantity()
        );
    }

    private MutateCartResponseDTO mutateCartUpdateItem(UUID userId, MutateCartRequestDTO mutateCartRequestDTO) {
        ProductEntity productEntity = productRepository.findProductEntityById(mutateCartRequestDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + mutateCartRequestDTO.getProductId()));

        CartEntity cartEntity = cartRepository.findCartEntityByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
        CartItemsEntity cartItemsEntity = CartItemsEntity.builder()
                .cart(cartEntity)
                .product(productEntity)
                .quantity(mutateCartRequestDTO.getQuantity())
                .build();
        cartItemRepository.save(cartItemsEntity);

        return new MutateCartResponseDTO(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice().multiply(BigDecimal.valueOf(mutateCartRequestDTO.getQuantity())),
                mutateCartRequestDTO.getQuantity()
        );
    }

    public MutateCartResponseDTO mutateCart(MutateCartRequestDTO mutateCartRequestDTO) {
        UUID userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new UnauthorizedException("User is not authenticated.");

        boolean isUserAlreadyHaveCart = cartRepository.existsByUserId(userId);
        if (!isUserAlreadyHaveCart) return mutateCartInit(userId, mutateCartRequestDTO);

        CartEntity cartEntity = cartRepository.findCartEntityByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        UUID storeId = cartEntity.getStore().getId();
        boolean isSameStore = storeId.equals(mutateCartRequestDTO.getStoreId());

        if (isSameStore) return mutateCartUpdateItem(userId, mutateCartRequestDTO);
        else return null;
    }

    public void deleteCartItem(DeleteItemRequestDTO deleteItemRequestDTO) {
        boolean isLastProduct = deleteItemRequestDTO.isLastProduct();
        if (isLastProduct) {
            UUID userId = SecurityUtil.getCurrentUserId();
            CartEntity cartEntity = cartRepository.findCartEntityByUserId(userId)
                    .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));
            cartRepository.delete(cartEntity);
            return;
        };

        UUID itemId = deleteItemRequestDTO.getItemsId();
        cartItemRepository.deleteById(itemId);
    }
}

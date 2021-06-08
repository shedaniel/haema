package com.williambl.haema.client

import com.google.common.collect.ImmutableList
import com.williambl.haema.hunter.VampireHunterEntity
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.MobEntityRenderer
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
import net.minecraft.client.render.entity.model.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
class VampireHunterEntityRenderer(context: EntityRendererFactory.Context) : MobEntityRenderer<VampireHunterEntity, VampireHunterModel>(context, VampireHunterModel(context.getPart(VampireHunterModel.layer)), 0.5f) {
    private val texture = Identifier("textures/entity/illager/pillager.png")

    init {
        addFeature(HeadFeatureRenderer(this, context.modelLoader))
        addFeature(HeldItemFeatureRenderer(this))
    }

    override fun scale(entity: VampireHunterEntity, matrixStack: MatrixStack, f: Float) {
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f)
    }

    override fun getTexture(entity: VampireHunterEntity?): Identifier = texture
}

@Environment(EnvType.CLIENT)
class VampireHunterModel(root: ModelPart) : CompositeEntityModel<VampireHunterEntity>(), ModelWithArms, ModelWithHead {
    private val head: ModelPart = root.getChild(EntityModelPartNames.HEAD)
    private val hat: ModelPart = head.getChild(EntityModelPartNames.HAT)
    private val arms: ModelPart = root.getChild(EntityModelPartNames.ARMS)
    private val leftLeg: ModelPart = root.getChild(EntityModelPartNames.LEFT_LEG)
    private val rightLeg: ModelPart = root.getChild(EntityModelPartNames.RIGHT_LEG)
    private val rightAttackingArm: ModelPart = root.getChild(EntityModelPartNames.RIGHT_ARM)
    private val leftAttackingArm: ModelPart = root.getChild(EntityModelPartNames.LEFT_ARM)
    init {
        hat.visible = false
    }

    override fun getParts(): Iterable<ModelPart> {
        return ImmutableList.of(head, hat, rightLeg, leftLeg, arms, rightAttackingArm, leftAttackingArm)
    }

    override fun setAngles(entity: VampireHunterEntity, f: Float, g: Float, h: Float, i: Float, j: Float) {
        head.yaw = i * 0.017453292f
        head.pitch = j * 0.017453292f
        arms.pivotY = 3.0f
        arms.pivotZ = -1.0f
        arms.pitch = -0.75f
        if (riding) {
            rightAttackingArm.pitch = -0.62831855f
            rightAttackingArm.yaw = 0.0f
            rightAttackingArm.roll = 0.0f
            leftAttackingArm.pitch = -0.62831855f
            leftAttackingArm.yaw = 0.0f
            leftAttackingArm.roll = 0.0f
            rightLeg.pitch = -1.4137167f
            rightLeg.yaw = 0.31415927f
            rightLeg.roll = 0.07853982f
            leftLeg.pitch = -1.4137167f
            leftLeg.yaw = -0.31415927f
            leftLeg.roll = -0.07853982f
        } else {
            rightAttackingArm.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 2.0f * g * 0.5f
            rightAttackingArm.yaw = 0.0f
            rightAttackingArm.roll = 0.0f
            leftAttackingArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f
            leftAttackingArm.yaw = 0.0f
            leftAttackingArm.roll = 0.0f
            rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f
            rightLeg.yaw = 0.0f
            rightLeg.roll = 0.0f
            leftLeg.pitch = MathHelper.cos(f * 0.6662f + 3.1415927f) * 1.4f * g * 0.5f
            leftLeg.yaw = 0.0f
            leftLeg.roll = 0.0f
        }
        if (entity.isHolding(Items.CROSSBOW)) {
            if (entity.isCharging()) {
                CrossbowPosing.charge(rightAttackingArm, leftAttackingArm, entity, true)
            }
            else {
                CrossbowPosing.hold(rightAttackingArm, leftAttackingArm, head, true)
            }
        } else if (entity.isAttacking) {
            // hold arm up in the air
            CrossbowPosing.meleeAttack(rightAttackingArm, leftAttackingArm, entity, handSwingProgress, h)
        }
    }

    private fun getArm(arm: Arm): ModelPart {
        return if (arm == Arm.LEFT) leftAttackingArm else rightAttackingArm
    }

    override fun getHead(): ModelPart {
        return head
    }

    override fun setArmAngle(arm: Arm, matrices: MatrixStack) {
        getArm(arm).rotate(matrices)
    }

    companion object {
        val layer = EntityModelLayer(Identifier("haema:vampire_hunter"), "main")

        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            val modelPartData2 = modelPartData.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )
            modelPartData2.addChild(
                EntityModelPartNames.HAT,
                ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, Dilation(0.45f)),
                ModelTransform.NONE
            )
            modelPartData2.addChild(
                EntityModelPartNames.NOSE,
                ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f),
                ModelTransform.pivot(0.0f, -2.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).uv(0, 38)
                    .cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, Dilation(0.5f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )
            val modelPartData3 = modelPartData.addChild(
                EntityModelPartNames.ARMS,
                ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(40, 38)
                    .cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f),
                ModelTransform.of(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f)
            )
            modelPartData3.addChild(
                "left_shoulder",
                ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f),
                ModelTransform.NONE
            )
            modelPartData.addChild(
                EntityModelPartNames.RIGHT_LEG,
                ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-2.0f, 12.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.LEFT_LEG,
                ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(2.0f, 12.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.RIGHT_ARM,
                ModelPartBuilder.create().uv(40, 46).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-5.0f, 2.0f, 0.0f)
            )
            modelPartData.addChild(
                EntityModelPartNames.LEFT_ARM,
                ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(5.0f, 2.0f, 0.0f)
            )
            return TexturedModelData.of(modelData, 64, 64)
        }
    }
}